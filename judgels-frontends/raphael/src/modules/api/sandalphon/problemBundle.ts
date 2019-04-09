import { ProblemStatement } from './problem';

export enum ItemType {
  Statement = 'STATEMENT',
  MultipleChoice = 'MULTIPLE_CHOICE',
  ShortAnswer = 'SHORT_ANSWER',
  Essay = 'ESSAY',
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

export interface ItemShortAnswerConfig extends ItemConfig {
  score: number;
  penalty: number;
  inputValidationRegex: string;
  gradingRegex: string;
}

export interface ItemEssayConfig extends ItemConfig {
  score: number;
}

export interface Item {
  jid: string;
  type: ItemType;
  number?: number;
  meta: string;
  config: ItemConfig;
  disabled: boolean;
}

export interface ProblemWorksheet {
  statement: ProblemStatement;
  reasonNotAllowedToSubmit?: string;
  items: Item[];
}
