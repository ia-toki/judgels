import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { ContestRole } from '../../../../modules/api/uriel/contestWeb';
import contestReducer from '../modules/contestReducer';
import ContestsPage from './ContestsPage';

import * as contestActions from '../modules/contestActions';

jest.mock('../modules/contestActions');

describe('ContestsPage', () => {
  let wrapper;
  let contests;

  const render = async () => {
    contestActions.getContests.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: contests,
        },
        rolesMap: {
          contestJid1: ContestRole.Contestant,
          contestJid2: ContestRole.None,
        },
        config: {
          canAdminister: false,
        },
      })
    );

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/contests']}>
          <Route path="/contests" component={ContestsPage} />
        </MemoryRouter>
      </Provider>
    );
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there are no contests', () => {
    beforeEach(async () => {
      contests = [];
      await render();
    });

    it('shows placeholder text and no contests', () => {
      expect(wrapper.text()).toContain('No contests.');
      expect(wrapper.find('a.content-card')).toHaveLength(0);
    });
  });

  describe('when there are contests', () => {
    beforeEach(async () => {
      contests = [
        {
          jid: 'contestJid1',
          slug: 'contest-1',
          name: 'Contest 1',
        },
        {
          jid: 'contestJid2',
          slug: 'contest-2',
          name: 'Contest 2',
        },
      ];
      await render();
    });

    it('shows the contests', () => {
      const cards = wrapper.find('a.content-card');
      expect(cards.map(card => [card.find('h4').text(), card.props().href])).toEqual([
        ['Contest 1CONTESTANT', '/contests/contest-1'],
        ['Contest 2', '/contests/contest-2'],
      ]);
    });
  });
});
