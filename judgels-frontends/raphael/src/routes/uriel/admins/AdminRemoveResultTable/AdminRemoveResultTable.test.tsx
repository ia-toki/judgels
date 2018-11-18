import { ReactWrapper, mount } from 'enzyme';
import * as React from 'react';
import { MemoryRouter } from 'react-router';

import { AdminRemoveResultTable, AdminRemoveResultTableProps } from './AdminRemoveResultTable';

describe('AdminRemoveResultTable', () => {
  let wrapper: ReactWrapper<any, any>;
  beforeEach(() => {
    const props: AdminRemoveResultTableProps = {
      usernames: ['budi', 'andi', 'zoro'],
      deletedAdminProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
    };
    wrapper = mount(
      <MemoryRouter>
        <AdminRemoveResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = wrapper.find('table');

    const deletedAdminRows = tables.at(0).find('tbody');
    expect(deletedAdminRows.children()).toHaveLength(2);
    expect(deletedAdminRows.childAt(0).text()).toEqual('andi');
    expect(deletedAdminRows.childAt(1).text()).toEqual('budi');

    const unknownAdminRows = tables.at(1).find('tbody');
    expect(unknownAdminRows.children()).toHaveLength(1);
    expect(unknownAdminRows.childAt(0).text()).toEqual('zoro');
  });
});
