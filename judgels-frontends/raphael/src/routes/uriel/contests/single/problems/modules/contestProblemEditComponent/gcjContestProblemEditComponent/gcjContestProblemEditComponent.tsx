import * as React from 'react';

import { ContestProblemEditComponent } from '../contestProblemEditComponent';
import { GcjValidProblemsSetData } from './gcjContestProblemValidations';
import { GcjContestProblemProcessor } from './gcjContestProblemProcessor';

export default {
  validation: GcjValidProblemsSetData,
  processor: GcjContestProblemProcessor,
  format: <code>alias,slug,points[,status[,submissionsLimit]]</code>,
  example: <pre>{'A,hello,3\nB,tree,4,CLOSED\nC,flow,6,OPEN,20'}</pre>,
} as ContestProblemEditComponent;
