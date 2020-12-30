import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router';

import { ContestContestantAddResultTable } from './ContestContestantAddResultTable';

describe('ContestContestantAddResultTable', () => {
  let wrapper;
  beforeEach(() => {
    const props = {
      usernames: ['budi', 'caca', 'andi', 'dudi', 'zoro'],
      insertedContestantProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
      },
      alreadyContestantProfilesMap: {
        dudi: { username: 'dudi' },
        caca: { username: 'caca' },
      },
    };
    wrapper = mount(
      <MemoryRouter>
        <ContestContestantAddResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = wrapper.find('table');

    const insertedContestantRows = tables.at(0).find('tbody');
    expect(insertedContestantRows.children()).toHaveLength(2);
    expect(insertedContestantRows.childAt(0).text()).toEqual('andi');
    expect(insertedContestantRows.childAt(1).text()).toEqual('budi');

    const alreadyContestantRows = tables.at(1).find('tbody');
    expect(alreadyContestantRows.children()).toHaveLength(2);
    expect(alreadyContestantRows.childAt(0).text()).toEqual('caca');
    expect(alreadyContestantRows.childAt(1).text()).toEqual('dudi');

    const unknownContestantRows = tables.at(2).find('tbody');
    expect(unknownContestantRows.children()).toHaveLength(1);
    expect(unknownContestantRows.childAt(0).text()).toEqual('zoro');
  });
});
