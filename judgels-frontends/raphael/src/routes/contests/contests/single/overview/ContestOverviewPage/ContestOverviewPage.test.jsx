import { mount } from 'enzyme';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ContestOverviewPage from './ContestOverviewPage';
import sessionReducer from '../../../../../../modules/session/sessionReducer';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestActions from '../../../modules/contestActions';

jest.mock('../../../modules/contestActions');

describe('ContestOverviewPage', () => {
  let wrapper;

  const render = async () => {
    contestActions.getContestDescription.mockReturnValue(() => Promise.resolve('Contest description'));

    const store = createStore(
      combineReducers({
        session: sessionReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <ContestOverviewPage />
        </Provider>
      </IntlProvider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('description', () => {
    beforeEach(async () => {
      await render();
    });

    it('shows the description', () => {
      expect(wrapper.find('.html-text').text()).toEqual('Contest description');
    });
  });
});
