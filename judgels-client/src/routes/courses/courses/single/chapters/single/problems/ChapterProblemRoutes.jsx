import { Route, Routes } from 'react-router';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import ChapterProblemPage from './single/ChapterProblemPage/ChapterProblemPage';

function ChapterProblemRoutes() {
  return (
    <Routes>
      <Route path=":problemAlias/*" element={<ChapterProblemPage />} />
    </Routes>
  );
}

export default withBreadcrumb('Problems')(ChapterProblemRoutes);
