export interface Page<T> {
  totalData: number;
  data: T[];
}

export enum OrderDir {
  ASC = 'ASC',
  DESC = 'DESC',
}
