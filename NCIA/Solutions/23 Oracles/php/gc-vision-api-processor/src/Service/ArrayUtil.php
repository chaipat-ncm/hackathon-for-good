<?php


namespace App\Service;


class ArrayUtil
{
    /**
     * @param $key
     * @param array $array
     * @param null $nullFiller
     * @return mixed|null
     */
    public static function get($key, array $array, $nullFiller = null)
    {
        return $array[$key] ?? $nullFiller;
    }

    /**
     * @param $key
     * @param array $array
     * @return bool
     */
    public static function getBoolean($key, array $array): bool
    {
        $value = self::get($key, $array, null);
        return is_bool($value) && $value || strtolower($value) === 'true';
    }
}