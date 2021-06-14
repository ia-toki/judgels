import { mount } from 'enzyme';
import { act } from 'preact/test-utils';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import ContestFilesPage from './ContestFilesPage';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestFileActions from '../modules/contestFileActions';

jest.mock('../modules/contestFileActions');

describe('ContestFilesPage', () => {
  let wrapper;
  let files;

  const render = async () => {
    contestFileActions.uploadFile.mockReturnValue(() => Promise.resolve({}));
    contestFileActions.getFiles.mockReturnValue(() =>
      Promise.resolve({
        data: files,
        config: { canManage: true },
      })
    );

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestFilesPage />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there are no files', () => {
    beforeEach(async () => {
      files = [];
      await render();
    });

    it('shows placeholder text and no files', async () => {
      expect(wrapper.text()).toContain('No files.');
      expect(wrapper.find('tr')).toHaveLength(1 + 0);
    });
  });

  describe('when there are files', () => {
    beforeEach(async () => {
      files = [
        {
          name: 'editorial.pdf',
          size: 100,
          lastModifiedTime: 12345,
        },
        {
          name: 'solutions.zip',
          size: 100,
          lastModifiedTime: 12345,
        },
      ];
      await render();
    });

    it('shows the files', () => {
      expect(wrapper.find('tr')).toHaveLength(1 + 1 + 2);
    });
  });

  beforeEach(async () => {
    files = [];
    await render();
  });

  test('upload form', () => {
    act(() => {
      const file = wrapper.find('input[name="file"]');
      file.prop('onChange')({ target: { files: [{ name: 'editorial.txt', size: 1000 }] }, preventDefault: () => {} });
    });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(contestFileActions.uploadFile).toHaveBeenCalledWith('contestJid', {
      name: 'editorial.txt',
      size: 1000,
    });
  });
});
