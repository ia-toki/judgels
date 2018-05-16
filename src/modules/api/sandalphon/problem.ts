import { LanguageRestriction } from '../gabriel/language';

export interface ProblemStatement {
  name: string;
  timeLimit: number;
  memoryLimit: number;
  text: string;
  sourceKeys: { [key: string]: string };
  gradingEngine: string;
  gradingLanguageRestriction: LanguageRestriction;
}
