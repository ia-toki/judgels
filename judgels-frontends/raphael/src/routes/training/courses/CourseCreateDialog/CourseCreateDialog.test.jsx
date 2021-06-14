import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { CourseCreateDialog } from './CourseCreateDialog';

describe('CourseCreateDialog', () => {
  let onGetCourseConfig;
  let onCreateCourse;
  let wrapper;

  beforeEach(() => {
    onCreateCourse = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      onGetCourseConfig,
      onCreateCourse,
    };
    wrapper = mount(
      <Provider store={store}>
        <CourseCreateDialog {...props} />
      </Provider>
    );
  });

  test('create dialog form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const slug = wrapper.find('input[name="slug"]');
    slug.getDOMNode().value = 'new-course';
    slug.simulate('input');

    const name = wrapper.find('input[name="name"]');
    name.getDOMNode().value = 'New course';
    name.simulate('input');

    const description = wrapper.find('textarea[name="description"]');
    description.getDOMNode().value = 'New description';
    description.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateCourse).toHaveBeenCalledWith({
      slug: 'new-course',
      name: 'New course',
      description: 'New description',
    });
  });
});
