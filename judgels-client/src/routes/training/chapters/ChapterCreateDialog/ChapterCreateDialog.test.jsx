import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { ChapterCreateDialog } from './ChapterCreateDialog';

describe('ChapterCreateDialog', () => {
  let onGetChapterConfig;
  let onCreateChapter;
  let wrapper;

  beforeEach(async () => {
    onCreateChapter = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      onGetChapterConfig,
      onCreateChapter,
    };
    wrapper = mount(
      <Provider store={store}>
        <ChapterCreateDialog {...props} />
      </Provider>
    );
  });

  test('create dialog form', () => {
    act(() => {
      const button = wrapper.find('button');
      button.simulate('click');
    });

    wrapper.update();

    act(() => {
      const name = wrapper.find('input[name="name"]');
      name.prop('onChange')({ target: { value: 'New Chapter' } });

      const form = wrapper.find('form');
      form.simulate('submit');
    });

    expect(onCreateChapter).toHaveBeenCalledWith({ name: 'New Chapter' });
  });
});
