import { ContestStyle } from 'modules/api/uriel/contest';

import defaultContestProblemEditComponent from './defaultContestProblemEditComponent/defaultContestProblemEditComponent';
import gcjContestProblemEditComponent from './gcjContestProblemEditComponent/gcjContestProblemEditComponent';

const contestProblemEditComponentMapping = {
  [ContestStyle.GCJ]: gcjContestProblemEditComponent,
};

export const getContestProblemEditComponent = (contestStyle: ContestStyle) => {
  return contestProblemEditComponentMapping[contestStyle] || defaultContestProblemEditComponent;
};
