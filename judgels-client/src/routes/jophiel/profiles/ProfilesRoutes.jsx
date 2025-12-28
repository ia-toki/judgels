import { withBreadcrumb } from '../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import SingleProfileDataRoute from './single/SingleProfileDataRoute';
import SingleProfileRoutes from './single/SingleProfileRoutes';

function ProfileRoutes() {
  return (
    <div>
      <SingleProfileDataRoute />
      <SingleProfileRoutes />
    </div>
  );
}

export default withBreadcrumb('Profiles')(ProfileRoutes);
