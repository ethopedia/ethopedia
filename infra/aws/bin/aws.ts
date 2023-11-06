#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import {BackendStack} from '../lib/backend-stack';

const app = new cdk.App();

const prod = {
  account: '655823636281',
  region: 'us-east-1'
}

new BackendStack(app, 'Backend', {
  env: prod
});
