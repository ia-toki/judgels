import { ContestProblemProcessor } from './contestProblemProcessor';

export interface ContestProblemEditComponent {
  validator: (value: any) => string | undefined;
  processor: ContestProblemProcessor;
  format: JSX.Element;
  example: JSX.Element;
}
