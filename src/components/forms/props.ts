import { FormInputMeta } from './meta';

export interface FormInputProps {
  input: {
    name: string;
  };
  inputHelper?: string;
  meta: FormInputMeta;
  label: string;
  labelHelper?: string;
}
