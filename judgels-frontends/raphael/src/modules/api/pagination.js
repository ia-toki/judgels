export interface Page<T> {
  totalCount: number;
  page: T[];
}

export enum OrderDir {
  ASC = 'ASC',
  DESC = 'DESC',
}
