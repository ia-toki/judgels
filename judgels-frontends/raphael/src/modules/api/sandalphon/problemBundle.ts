import { ProblemStatement } from './problem';

export enum ItemType {
  Statement = 'STATEMENT',
  MultipleChoice = 'MULTIPLE_CHOICE',
}

export interface Item {
  jid: string;
  type: ItemType;
  number?: number;
  meta: string;
  config: string;
}

export interface ProblemWorksheet {
  statement: ProblemStatement;
  reasonNotAllowedToSubmit?: string;
  items: Item[];
}
