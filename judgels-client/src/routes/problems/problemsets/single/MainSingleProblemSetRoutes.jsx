import { Route, Routes } from 'react-router-dom';

import SingleProblemSetDataRoute from './SingleProblemSetDataRoute';
import SingleProblemSetRoutes from './SingleProblemSetRoutes';
import MainSingleProblemSetProblemRoutes from './problems/single/MainSingleProblemSetProblemRoutes';

function MainSingleProblemSetRoutes() {
  return (
    <div>
      <SingleProblemSetDataRoute />
      <Routes>
        <Route path=":problemAlias/*" element={<MainSingleProblemSetProblemRoutes />} />
        <Route path="*" element={<SingleProblemSetRoutes />} />
      </Routes>
    </div>
  );
}

export default MainSingleProblemSetRoutes;
