export interface Page<T> {
  currentPage: number;
  pageSize: number;
  totalPages: number;
  totalData: number;
  data: T[];
}
