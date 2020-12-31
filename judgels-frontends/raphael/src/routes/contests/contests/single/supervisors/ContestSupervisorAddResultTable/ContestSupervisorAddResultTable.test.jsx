import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router';

import { ContestSupervisorAddResultTable } from './ContestSupervisorAddResultTable';

describe('ContestSupervisorAddResultTable', () => {
  let wrapper;
  beforeEach(() => {
    const props = {
      usernames: ['budi', 'caca', 'andi', 'dudi', 'zoro'],
      insertedSupervisorProfilesMap: {
        budi: { username: 'budi' },
        andi: { username: 'andi' },
        caca: { username: 'caca' },
        dudi: { username: 'dudi' },
      },
    };
    wrapper = mount(
      <MemoryRouter>
        <ContestSupervisorAddResultTable {...props} />
      </MemoryRouter>
    );
  });

  it('shows the correct tables', () => {
    const tables = wrapper.find('table');

    const insertedSupervisorRows = tables.at(0).find('tbody');
    expect(insertedSupervisorRows.children()).toHaveLength(4);
    expect(insertedSupervisorRows.childAt(0).text()).toEqual('andi');
    expect(insertedSupervisorRows.childAt(1).text()).toEqual('budi');
    expect(insertedSupervisorRows.childAt(2).text()).toEqual('caca');
    expect(insertedSupervisorRows.childAt(3).text()).toEqual('dudi');

    const unknownSupervisorRows = tables.at(1).find('tbody');
    expect(unknownSupervisorRows.children()).toHaveLength(1);
    expect(unknownSupervisorRows.childAt(0).text()).toEqual('zoro');
  });
});
