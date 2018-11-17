import { ReactWrapper, mount } from 'enzyme';
import * as React from 'react';
import { MemoryRouter } from 'react-router';

import {
  ContestContestantRemoveResultTable,
  ContestContestantRemoveResultTableProps,
} from './ContestContestantRemoveResultTable';

describe('ContestContestantsRemoveResultTable', () => {
  let wrapper: ReactWrapper<any, any>;
  beforeEach(() => {
    const props: ContestContestantRemoveResultTableProps = {
      usernames: ['budi', 'andi', 'zoro'],
      deletedContestantProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
    };
    wrapper = mount(
      <MemoryRouter>
        <ContestContestantRemoveResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = wrapper.find('table');

    const deletedContestantRows = tables.at(0).find('tbody');
    expect(deletedContestantRows.children()).toHaveLength(2);
    expect(deletedContestantRows.childAt(0).text()).toEqual('andi');
    expect(deletedContestantRows.childAt(1).text()).toEqual('budi');

    const unknownContestantRows = tables.at(1).find('tbody');
    expect(unknownContestantRows.children()).toHaveLength(1);
    expect(unknownContestantRows.childAt(0).text()).toEqual('zoro');
  });
});
