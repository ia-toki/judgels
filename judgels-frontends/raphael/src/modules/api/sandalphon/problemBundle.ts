export enum ItemType {
  Statement = 'STATEMENT',
  MultipleChoice = 'MULTIPLE_CHOICE',
}

export interface Item {
  jid: string;
  type: ItemType;
  meta: string;
  config: string;
}

export interface ProblemWorksheet {
  reasonNotAllowedToSubmit?: string;
  items: Item[];
}
