import * as React from 'react';

import './DetailPage.css';
import { FullPageLayout } from '../layouts/FullPageLayout/FullPageLayout';

export interface DetailPageProps {
  className?: string;
  name: string;
  description?: string;
  mainContent: any;
  sideContent: any;
}

export const DetailPage = (props: DetailPageProps) => (
  <FullPageLayout>
    <h2>{props.name}</h2>
    <p>{props.description}</p>
    <hr />
    <div className="detail-page">
      <div className="detail-page__main">{props.mainContent}</div>
      <div className="detail-page__side">{props.sideContent}</div>
    </div>
  </FullPageLayout>
);
