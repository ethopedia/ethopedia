import * as cdk from 'aws-cdk-lib';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import {Construct} from 'constructs';
import * as path from "path";

export class AwsStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const api = new apigateway.RestApi(this, 'api', {
      description: 'Ethopedia main api gateway',
      deployOptions: {
        stageName: 'prod',
      },
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
    });

    // 👇 create an Output for the API URL
    new cdk.CfnOutput(this, 'apiUrl', {value: api.url});

    // 👇 define GET todos function
    const getTodosLambda = new lambda.Function(this, 'default-api', {
      runtime: lambda.Runtime.JAVA_17,
      handler: 'org.ethopedia.apps.api.DefaultHandlerKt::',
      code: lambda.Code.fromAsset(path.join(__dirname, '../')),
    });

    // 👇 add a /todos resource
    const todos = api.root.addResource('todos');

    // 👇 integrate GET /todos with getTodosLambda
    todos.addMethod(
      'GET',
      new apigateway.LambdaIntegration(getTodosLambda, {proxy: true}),
    );
  }
}
