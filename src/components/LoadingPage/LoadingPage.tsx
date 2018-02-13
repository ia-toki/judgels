import { Spinner } from '@blueprintjs/core';
import * as React from 'react';

import './LoadingPage.css';
import { SingleColumnLayout } from '../layouts/SingleColumnLayout/SingleColumnLayout';

export const LoadingPage = () => (
  <SingleColumnLayout>
    <Spinner className="loading-spinner" />
  </SingleColumnLayout>
);
