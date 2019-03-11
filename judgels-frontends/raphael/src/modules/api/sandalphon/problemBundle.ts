import { ProblemStatement } from './problem';

export enum ItemType {
  Statement = 'STATEMENT',
  MultipleChoice = 'MULTIPLE_CHOICE',
}

export interface ItemConfig {
  statement: string;
}

export interface ItemMultipleChoiceConfig extends ItemConfig {
  choices: {
    alias: string;
    content: string;
  }[];
}

export interface Item {
  jid: string;
  type: ItemType;
  number?: number;
  meta: string;
  config: ItemConfig;
}

export interface ProblemWorksheet {
  statement: ProblemStatement;
  reasonNotAllowedToSubmit?: string;
  items: Item[];
}
