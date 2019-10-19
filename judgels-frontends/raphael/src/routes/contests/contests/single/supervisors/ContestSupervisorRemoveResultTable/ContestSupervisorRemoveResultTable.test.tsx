import { ReactWrapper, mount } from 'enzyme';
import * as React from 'react';
import { MemoryRouter } from 'react-router';

import {
  ContestSupervisorRemoveResultTable,
  ContestSupervisorRemoveResultTableProps,
} from './ContestSupervisorRemoveResultTable';

describe('ContestSupervisorRemoveResultTable', () => {
  let wrapper: ReactWrapper<any, any>;
  beforeEach(() => {
    const props: ContestSupervisorRemoveResultTableProps = {
      usernames: ['budi', 'andi', 'zoro'],
      deletedSupervisorProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
    };
    wrapper = mount(
      <MemoryRouter>
        <ContestSupervisorRemoveResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = wrapper.find('table');

    const deletedSupervisorRows = tables.at(0).find('tbody');
    expect(deletedSupervisorRows.children()).toHaveLength(2);
    expect(deletedSupervisorRows.childAt(0).text()).toEqual('andi');
    expect(deletedSupervisorRows.childAt(1).text()).toEqual('budi');

    const unknownSupervisorRows = tables.at(1).find('tbody');
    expect(unknownSupervisorRows.children()).toHaveLength(1);
    expect(unknownSupervisorRows.childAt(0).text()).toEqual('zoro');
  });
});
