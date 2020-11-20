import { mount } from 'enzyme';
import * as React from 'react';
import { MemoryRouter } from 'react-router';

import { ContestManagerRemoveResultTable } from './ContestManagerRemoveResultTable';

describe('ContestManagerRemoveResultTable', () => {
  let wrapper;
  beforeEach(() => {
    const props = {
      usernames: ['budi', 'andi', 'zoro'],
      deletedManagerProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
    };
    wrapper = mount(
      <MemoryRouter>
        <ContestManagerRemoveResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = wrapper.find('table');

    const deletedManagerRows = tables.at(0).find('tbody');
    expect(deletedManagerRows.children()).toHaveLength(2);
    expect(deletedManagerRows.childAt(0).text()).toEqual('andi');
    expect(deletedManagerRows.childAt(1).text()).toEqual('budi');

    const unknownManagerRows = tables.at(1).find('tbody');
    expect(unknownManagerRows.children()).toHaveLength(1);
    expect(unknownManagerRows.childAt(0).text()).toEqual('zoro');
  });
});
