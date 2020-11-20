import { ContestStyle } from '../../../../../../../modules/api/uriel/contest';

import defaultContestProblemEditor from './defaultContestProblemEditor';
import gcjContestProblemEditor from './gcjContestProblemEditor';

const contestProblemEditorMapping = {
  [ContestStyle.GCJ]: gcjContestProblemEditor,
};

export function getContestProblemEditor(contestStyle) {
  return contestProblemEditorMapping[contestStyle] || defaultContestProblemEditor;
}
