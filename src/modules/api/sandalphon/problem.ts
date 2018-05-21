import { LanguageRestriction } from '../gabriel/language';

export interface ProblemStatement {
  name: string;
  timeLimit: number;
  memoryLimit: number;
  text: string;
}

export interface ProblemSubmissionConfig {
  sourceKeys: { [key: string]: string };
  gradingEngine: string;
  gradingLanguageRestriction: LanguageRestriction;
}

export interface ProblemWorksheet {
  statement: ProblemStatement;
  submissionConfig: ProblemSubmissionConfig;
  reasonNotAllowedToSubmit?: string;
}
