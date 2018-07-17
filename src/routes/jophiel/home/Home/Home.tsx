import { Card, Icon } from '@blueprintjs/core';
import * as React from 'react';

import { APP_CONFIG } from '../../../../conf';
import { SingleColumnLayout } from '../../../../components/layouts/SingleColumnLayout/SingleColumnLayout';

import './Home.css';

const redirectToUriel = () => (location.href = APP_CONFIG.tempHome.urielUrl);
const redirectToJerahmeel = () => (location.href = APP_CONFIG.tempHome.jerahmeelUrl);

export const Home = () => (
  <SingleColumnLayout>
    <h5 className="home__text">
      <Icon iconName="oil-field" className="home__icon" />
    </h5>
    <h5 className="home__text">Apologies! The new {APP_CONFIG.name} site is still under construction.</h5>
    <p className="home__text home__text--small">Until then, you can access the legacy modules below:</p>
    <br />
    <Card interactive onClick={redirectToUriel}>
      <p className="home__link">
        <Icon iconName="walk" /> Competition Gate
      </p>
    </Card>
    <br />
    <Card interactive onClick={redirectToJerahmeel}>
      <p className="home__link">
        <Icon iconName="pulse" /> Training Gate
      </p>
    </Card>
  </SingleColumnLayout>
);
