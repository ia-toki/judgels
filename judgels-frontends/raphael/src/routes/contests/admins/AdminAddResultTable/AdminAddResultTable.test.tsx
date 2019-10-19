import { ReactWrapper, mount } from 'enzyme';
import * as React from 'react';
import { MemoryRouter } from 'react-router';

import { AdminAddResultTable, AdminAddResultTableProps } from './AdminAddResultTable';

describe('AdminAddResultTable', () => {
  let wrapper: ReactWrapper<any, any>;
  beforeEach(() => {
    const props: AdminAddResultTableProps = {
      usernames: ['budi', 'caca', 'andi', 'dudi', 'zoro'],
      insertedAdminProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
      alreadyAdminProfilesMap: {
        dudi: { username: 'dudi' },
        caca: { username: 'caca' },
      },
    };
    wrapper = mount(
      <MemoryRouter>
        <AdminAddResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = wrapper.find('table');

    const insertedAdminRows = tables.at(0).find('tbody');
    expect(insertedAdminRows.children()).toHaveLength(2);
    expect(insertedAdminRows.childAt(0).text()).toEqual('andi');
    expect(insertedAdminRows.childAt(1).text()).toEqual('budi');

    const alreadyAdminRows = tables.at(1).find('tbody');
    expect(alreadyAdminRows.children()).toHaveLength(2);
    expect(alreadyAdminRows.childAt(0).text()).toEqual('caca');
    expect(alreadyAdminRows.childAt(1).text()).toEqual('dudi');

    const unknownAdminRows = tables.at(2).find('tbody');
    expect(unknownAdminRows.children()).toHaveLength(1);
    expect(unknownAdminRows.childAt(0).text()).toEqual('zoro');
  });
});
