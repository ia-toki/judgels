export interface ContestList {
  totalItems: number;
  totalPages: number;
  data: Contest[];
}

export interface Contest {
  name: string;
}

export const contestListMock = {
  totalItems: 100,
  totalPages: 5,
  data: [{ name: 'TOKI Open Contest - April 2017' }, { name: 'TOKI Open Contest - Mei 2017' }],
};
