import { ContestProblemProcessor } from './contestProblemProcessor';

export interface ContestProblemEditComponent {
  validation: (value: any) => string | undefined;
  processor: ContestProblemProcessor;
  format: JSX.Element;
  example: JSX.Element;
}
