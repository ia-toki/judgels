import * as React from 'react';

import './SingleColumnLayout.css';

export const SingleColumnLayout = (props: { children: any }) => (
  <div className="layout-single-col">{props.children}</div>
);
