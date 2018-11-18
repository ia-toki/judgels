import * as React from 'react';
import { Route, withRouter } from 'react-router';

import { withBreadcrumb } from 'components/BreadcrumbWrapper/BreadcrumbWrapper';

import AdminsPage from './AdminsPage/AdminsPage';

const AdminsRoutes = () => (
  <div>
    <Route exact path="/contests/_admins" component={AdminsPage} />
  </div>
);

export default withRouter<any>(withBreadcrumb('Admins')(AdminsRoutes));
