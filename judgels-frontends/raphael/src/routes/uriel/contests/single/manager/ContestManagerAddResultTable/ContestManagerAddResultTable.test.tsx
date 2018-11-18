import { ReactWrapper, mount } from 'enzyme';
import * as React from 'react';
import { MemoryRouter } from 'react-router';

import { ContestManagerAddResultTable, ContestManagerAddResultTableProps } from './ContestManagerAddResultTable';

describe('AdminAddResultTable', () => {
  let wrapper: ReactWrapper<any, any>;
  beforeEach(() => {
    const props: ContestManagerAddResultTableProps = {
      usernames: ['budi', 'caca', 'andi', 'dudi', 'zoro'],
      insertedManagerProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
      alreadyManagerProfilesMap: {
        dudi: { username: 'dudi' },
        caca: { username: 'caca' },
      },
    };
    wrapper = mount(
      <MemoryRouter>
        <ContestManagerAddResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = wrapper.find('table');

    const insertedManagerRows = tables.at(0).find('tbody');
    expect(insertedManagerRows.children()).toHaveLength(2);
    expect(insertedManagerRows.childAt(0).text()).toEqual('andi');
    expect(insertedManagerRows.childAt(1).text()).toEqual('budi');

    const alreadyManagerRows = tables.at(1).find('tbody');
    expect(alreadyManagerRows.children()).toHaveLength(2);
    expect(alreadyManagerRows.childAt(0).text()).toEqual('caca');
    expect(alreadyManagerRows.childAt(1).text()).toEqual('dudi');

    const unknownManagerRows = tables.at(2).find('tbody');
    expect(unknownManagerRows.children()).toHaveLength(1);
    expect(unknownManagerRows.childAt(0).text()).toEqual('zoro');
  });
});
