import { Icon } from '@blueprintjs/core';
import * as React from 'react';

import { SingleColumnLayout } from '../../../../components/layouts/SingleColumnLayout/SingleColumnLayout';

import './HomePage.css';

export const HomePage = () => (
  <SingleColumnLayout>
    <h3 className="home__text">
      <Icon icon="oil-field" className="home__icon" iconSize={Icon.SIZE_LARGE} />
    </h3>
    <h3 className="home__text">Dashboard under construction.</h3>
  </SingleColumnLayout>
);
