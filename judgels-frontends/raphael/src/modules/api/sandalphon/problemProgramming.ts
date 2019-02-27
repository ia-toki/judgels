import { LanguageRestriction } from '../gabriel/language';
import { ProblemStatement } from './problem';

export interface ProblemLimits {
  timeLimit: number;
  memoryLimit: number;
}

export interface ProblemSubmissionConfig {
  sourceKeys: { [key: string]: string };
  gradingEngine: string;
  gradingLanguageRestriction: LanguageRestriction;
}

export interface ProblemWorksheet {
  statement: ProblemStatement;
  limits: ProblemLimits;
  submissionConfig: ProblemSubmissionConfig;
  reasonNotAllowedToSubmit?: string;
}
