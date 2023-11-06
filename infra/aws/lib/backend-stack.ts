import * as cdk from 'aws-cdk-lib';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as route53 from 'aws-cdk-lib/aws-route53';
import {ARecord, CnameRecord, HostedZone, RecordTarget} from 'aws-cdk-lib/aws-route53';
import * as route53targets from 'aws-cdk-lib/aws-route53-targets';
import {Construct} from 'constructs';
import * as path from "path";
import {BasePathMapping, DomainName, EndpointType} from "aws-cdk-lib/aws-apigateway";
import {Certificate, CertificateValidation} from "aws-cdk-lib/aws-certificatemanager";
import {Duration} from "aws-cdk-lib";

export class BackendStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const ethopediaDomain = HostedZone.fromLookup(this, 'EthopediaZone', {
      domainName: 'ethopedia.org'
    })

    const certificate = new Certificate(this, 'ApiCert', {
      domainName: '*.ethopedia.org',
      validation: CertificateValidation.fromDns(ethopediaDomain)
    })

    const customDomain = new DomainName(this, 'customDomain', {
      domainName: 'api.ethopedia.org',
      certificate: certificate,
      endpointType: EndpointType.REGIONAL
    })

    const api = new apigateway.LambdaRestApi(this, 'api', {
      description: 'Ethopedia main api gateway',
      // 👇 enable CORS
      defaultCorsPreflightOptions: {
        allowHeaders: [
          'Content-Type',
          'X-Amz-Date',
          'Authorization',
          'X-Api-Key',
        ],
        allowMethods: ['OPTIONS', 'GET', 'POST', 'PUT', 'PATCH', 'DELETE'],
        allowCredentials: true,
        allowOrigins: ['*'],
      },
      handler: new lambda.Function(this, 'default-api', {
        runtime: lambda.Runtime.JAVA_17,
        handler: 'org.ethopedia.apps.api.DefaultHandler',
        code: lambda.Code.fromAsset(path.join(__dirname, '../../../platform/apps/api/build/libs/api-main-SNAPSHOT-all.jar')),
        timeout: Duration.seconds(20),
        memorySize: 1024
      })
    });

    // 👇 create an Output for the API URL
    new cdk.CfnOutput(this, 'apiUrl', {value: api.url});

    new BasePathMapping(this, 'CustomBasePathMapping', {
      domainName: customDomain,
      restApi: api
    })

    new ARecord(this, 'ApiRecord', {
      zone: ethopediaDomain,
      recordName: 'api',
      target: route53.RecordTarget.fromAlias(
        new route53targets.ApiGatewayDomain(customDomain)
      )
    })

    const d = new ARecord(this, 'VercelSiteRecord', {
      zone: ethopediaDomain,
      recordName: 'www',
      target: RecordTarget.fromValues('76.76.21.21')
    })

    new ARecord(this, 'NakedSiteRedirect', {
      zone: ethopediaDomain,
      recordName: '',
      target: RecordTarget.fromAlias(
        new route53targets.Route53RecordTarget(d)
      )
    })
  }
}
