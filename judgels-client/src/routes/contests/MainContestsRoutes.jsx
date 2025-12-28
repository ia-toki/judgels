import { Route, Routes } from 'react-router-dom';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ContestsRoutes from './ContestsRoutes';
import MainSingleContestRoutes from './contests/single/MainSingleContestRoutes';

function MainContestRoutes() {
  return (
    <div>
      <Routes>
        <Route path=":contestSlug/*" element={<MainSingleContestRoutes />} />
        <Route path="*" element={<ContestsRoutes />} />
      </Routes>
    </div>
  );
}

export default withBreadcrumb('Contests')(MainContestRoutes);
