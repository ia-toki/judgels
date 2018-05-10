import { Grading } from '../gabriel/grading';

export interface Submission {
  id: number;
  jid: string;
  userJid: string;
  problemJid: string;
  containerJid: string;
  gradingEngine: string;
  gradingLanguage: string;
  time: number;
  latestGrading?: Grading;
}
