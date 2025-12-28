import { Route, Routes } from 'react-router-dom';

import { withBreadcrumb } from '../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ProblemsRoutes from './ProblemsRoutes';
import ProblemsPage from './problems/ProblemsPage/ProblemsPage';
import ProblemSetsPage from './problemsets/ProblemSetsPage/ProblemSetsPage';
import MainSingleProblemSetRoutes from './problemsets/single/MainSingleProblemSetRoutes';

function MainProblemsRoutes() {
  return (
    <Routes>
      <Route
        path="problemsets"
        element={
          <ProblemsRoutes>
            <ProblemSetsPage />
          </ProblemsRoutes>
        }
      />
      <Route path=":problemSetSlug/*" element={<MainSingleProblemSetRoutes />} />
      <Route
        index
        element={
          <ProblemsRoutes>
            <ProblemsPage />
          </ProblemsRoutes>
        }
      />
    </Routes>
  );
}

export default withBreadcrumb('Problems')(MainProblemsRoutes);
