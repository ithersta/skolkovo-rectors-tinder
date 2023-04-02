<script lang="ts">
    import Input from './lib/Input.svelte'

    const urlParams = new URLSearchParams(window.location.search)
    const cities = urlParams.get("cities").split('|')
    const webApp = window.Telegram.WebApp
    webApp.MainButton.onClick(() => webApp.sendData(selectedCity))

    let inputValue = ""
    let selectedCity = null
    $: filteredCities = cities.filter(city => city.toLowerCase().match(inputValue.toLowerCase()))
    $: if (selectedCity !== null) {
        webApp.MainButton.show()
    }

    function onCitySelected(city: string) {
        selectedCity = city
    }
</script>

<main>
    <Input bind:inputValue/>
    {#each filteredCities as city}
        <p class="mx-2 px-2 py-3 w-auto text-slate-900 justify-start rounded-md {city === selectedCity ? 'bg-blue-200' : ''}"
           on:click={() => onCitySelected(city)}>{city}</p>
    {/each}
</main>
