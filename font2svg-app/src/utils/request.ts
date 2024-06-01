export interface IResponse<T> {
  code: number;
  message: string;
  data?: T;
}

export async function request<T>(url: string, options: RequestInit): Promise<T> {

  const resp = await fetch(url, options)
  if (resp.status >= 400) {
    throw new Error(resp.statusText)
  }
  const result: IResponse<T> = await resp.json()
  if (result.code !== 0) {
    throw new Error(result.message)
  }
  return result.data!;
}