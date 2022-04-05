export interface Todo {
  id: number;
  content: string;
}

export interface Meta {
  totalCount: number;
}

export interface QValidate extends HTMLElement {
  validate: () => boolean;
  hasError: boolean;
}
