import { Route, Routes } from 'react-router-dom';

import ContestProblemsPage from './ContestProblemsPage/ContestProblemsPage';
import ContestProblemPage from './single/ContestProblemPage/ContestProblemPage';

export default function ContestProblemRoutes() {
  return (
    <div>
      <Routes>
        <Route index element={<ContestProblemsPage />} />
        <Route path=":problemAlias" element={<ContestProblemPage />} />
      </Routes>
    </div>
  );
}
