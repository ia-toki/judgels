import { Tab2, Tabs2 } from '@blueprintjs/core';
import * as React from 'react';

import './Menubar.css';

export const Menubar = () => (
  <div className="menubar">
    <div className="menubar__content">
      <Tabs2 id="menubar" renderActiveTabPanelOnly>
        <Tab2 id="home">Home</Tab2>
      </Tabs2>
    </div>
  </div>
);
