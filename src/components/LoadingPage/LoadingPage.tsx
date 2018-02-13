import { Spinner } from '@blueprintjs/core';
import * as React from 'react';

import { SingleColumnLayout } from '../layouts/SingleColumnLayout/SingleColumnLayout';

import './LoadingPage.css';

export const LoadingPage = () => (
  <SingleColumnLayout>
    <Spinner className="loading-spinner" />
  </SingleColumnLayout>
);
