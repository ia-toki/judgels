import * as React from 'react';
import { Card } from '../../../../../components/Card/Card';
import { Registrant } from '../../../../../modules/api/uriel/registrant';
import Pagination from '../../../../../components/Pagination/Pagination';
import './ContestRegistrantTable.css';

export interface ContestRegistrantTableProps {
  data: Registrant[];
}

export const ContestRegistrantTable = (props: ContestRegistrantTableProps) => {
  const onNext = (nextPage?: number) => undefined;
  const list = props.data.map((item, id) => (
    <div key={id} className="flex-row contest-registrant-table__item">
      {/* TODO add on click */}
      <img className="contest-registrant-table__avatar" src={'/avatar-default.png'} alt="Avatar" />
      <div className="contest-registrant-table__name">
        <p>{item.name}</p>
      </div>
    </div>
  ));
  return (
    <Card title="Registrants">
      <div>
        <Pagination currentPage={1} pageSize={10} totalData={100} onChangePage={onNext} />
      </div>
      <div>{list}</div>
    </Card>
  );
};
