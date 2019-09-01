import { ContestProblemData } from '../../../../../../../modules/api/uriel/contestProblem';

export interface ContestProblemEditor {
  validator: (value: any) => string | undefined;
  serializer: (problems: ContestProblemData[]) => string;
  deserializer: (problems: string) => ContestProblemData[];
  format: JSX.Element;
  example: JSX.Element;
}
