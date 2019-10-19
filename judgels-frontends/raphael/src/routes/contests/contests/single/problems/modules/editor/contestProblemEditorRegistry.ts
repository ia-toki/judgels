import { ContestStyle } from '../../../../../../../modules/api/uriel/contest';

import defaultContestProblemEditor from './defaultContestProblemEditor';
import gcjContestProblemEditor from './gcjContestProblemEditor';

const contestProblemEditorMapping = {
  [ContestStyle.GCJ]: gcjContestProblemEditor,
};

export const getContestProblemEditor = (contestStyle: ContestStyle) => {
  return contestProblemEditorMapping[contestStyle] || defaultContestProblemEditor;
};
