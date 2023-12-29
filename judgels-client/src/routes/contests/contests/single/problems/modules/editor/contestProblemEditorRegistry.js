import { ContestStyle } from '../../../../../../../modules/api/uriel/contest';
import defaultContestProblemEditor from './defaultContestProblemEditor';
import gcjContestProblemEditor from './gcjContestProblemEditor';
import trocContestProblemEditor from './trocContestProblemEditor';

const contestProblemEditorMapping = {
  [ContestStyle.TROC]: trocContestProblemEditor,
  [ContestStyle.GCJ]: gcjContestProblemEditor,
};

export function getContestProblemEditor(contestStyle) {
  return contestProblemEditorMapping[contestStyle] || defaultContestProblemEditor;
}
