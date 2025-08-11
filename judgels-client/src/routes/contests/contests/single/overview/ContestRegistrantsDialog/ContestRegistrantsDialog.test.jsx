import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestRegistrantsDialog from './ContestRegistrantsDialog';

import * as contestContestantActions from '../../modules/contestContestantActions';

jest.mock('../../modules/contestContestantActions');

describe('ContestRegistrantsDialog', () => {
  let wrapper;

  beforeEach(() => {
    contestContestantActions.getApprovedContestants.mockReturnValue(() =>
      Promise.resolve({
        data: ['userJid1', 'userJid2', 'userJid3', 'userJid4', 'userJid5', 'userJid6'],
        profilesMap: {
          userJid1: { country: 'TH', username: 'username1', rating: { publicRating: 2000 } },
          userJid2: { country: 'ID', username: 'username2', rating: { publicRating: 1000 } },
          userJid3: { country: 'ID', username: 'username3', rating: { publicRating: 3000 } },
          userJid4: { country: 'ID', username: 'username4', rating: { publicRating: 2000 } },
          userJid5: { country: 'ID', username: 'username5', rating: { publicRating: 1000 } },
          userJid6: { username: 'username6' },
        },
      })
    );

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestRegistrantsDialog />
        </MemoryRouter>
      </Provider>
    );
  });

  test('table', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    expect(wrapper.find('tr').map(tr => tr.find('td').map(td => td.text()))).toEqual([
      [],
      ['Indonesia', 'username3'],
      ['Indonesia', 'username4'],
      ['Thailand', 'username1'],
      ['Indonesia', 'username2'],
      ['Indonesia', 'username5'],
      ['', 'username6'],
    ]);
  });
});
