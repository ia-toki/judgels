import * as React from 'react';
import { Intent } from '@blueprintjs/core';

import { APP_CONFIG } from '../../../../conf';
import { ButtonLink } from '../../../../components/ButtonLink/ButtonLink';

import './WelcomePage.css';

const bannerImage = require('assets/images/welcome-banner.jpg');
const overlayImage = require('assets/images/welcome-overlay.png');

export const WelcomePage = () => (
  <div className="banner">
    <div>
      <img src={bannerImage} className="banner__image" />
    </div>
    <div>
      <img src={overlayImage} className="banner__image" />
    </div>
    <div className="banner__contents">
      <div>
        <h1 className="banner__text">{APP_CONFIG.welcomeBanner.title}</h1>
        <span className="banner__text">{APP_CONFIG.welcomeBanner.description}</span>
      </div>
      <div className="banner__buttons">
        <ButtonLink to="/register" intent={Intent.PRIMARY} className="banner__button">
          Register
        </ButtonLink>
        <ButtonLink to="/login" intent={Intent.NONE} className="banner__button">
          Log in
        </ButtonLink>
      </div>
    </div>
  </div>
);
