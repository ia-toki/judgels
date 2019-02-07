import * as React from 'react';

import { ContestProblemEditComponent } from '../contestProblemEditComponent';
import { DefaultValidProblemsSetData } from './defaultContestProblemValidations';
import { DefaultContestProblemProcessor } from './defaultContestProblemProcessor';

export default {
  validation: DefaultValidProblemsSetData,
  processor: DefaultContestProblemProcessor,
  format: <code>alias,slug[,status[,submissionsLimit]]</code>,
  example: <pre>{'A,hello\nB,tree,CLOSED\nC,flow,OPEN,20'}</pre>,
} as ContestProblemEditComponent;
